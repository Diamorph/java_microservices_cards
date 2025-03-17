package com.diamorph.cards.service.impl;

import com.diamorph.cards.constants.CardsConstants;
import com.diamorph.cards.dto.CardsDto;
import com.diamorph.cards.enitity.Cards;
import com.diamorph.cards.exception.CardAlreadyExistsException;
import com.diamorph.cards.exception.ResourceNotFoundException;
import com.diamorph.cards.mapper.CardsMapper;
import com.diamorph.cards.repository.CardsRepository;
import com.diamorph.cards.service.ICardsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CardsService implements ICardsService {

    private CardsRepository cardsRepository;

    @Override
    public void createCard(String mobileNumber) {
        Optional<Cards> optionalCards = cardsRepository.findByMobileNumber(mobileNumber);
        if (optionalCards.isPresent()) {
            throw new CardAlreadyExistsException("Card already registered with given mobileNumber " + mobileNumber);
        }
        cardsRepository.save(createNewCard(mobileNumber));
    }

    private Cards createNewCard(String mobileNumber) {
        Cards newCard = new Cards();
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        return newCard;
    }

    private Cards fetchCardByMobileNumberThrowResourceNotFound(String mobileNumber) {
        return cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
    }

    private Cards fetchCardByCardNumberThrowResourceNotFound(String cardNumber) {
        return cardsRepository.findByCardNumber(cardNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "CardNumber", cardNumber)
        );
    }

    @Override
    public CardsDto fetchCard(String mobileNumber) {
        Cards cards = this.fetchCardByMobileNumberThrowResourceNotFound(mobileNumber);
        return CardsMapper.mapToCardsDto(cards, new CardsDto());
    }

    @Override
    public boolean updateCard(CardsDto cardsDto) {
        Cards cards = this.fetchCardByCardNumberThrowResourceNotFound(cardsDto.getCardNumber());
        CardsMapper.mapToCards(cardsDto, cards);
        cardsRepository.save(cards);
        return true;
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        Cards cards = this.fetchCardByMobileNumberThrowResourceNotFound(mobileNumber);
        cardsRepository.deleteById(cards.getCardId());
        return true;
    }
}
