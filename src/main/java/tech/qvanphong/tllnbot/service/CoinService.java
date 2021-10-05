package tech.qvanphong.tllnbot.service;

import org.springframework.stereotype.Service;
import tech.qvanphong.tllnbot.entity.Coin;
import tech.qvanphong.tllnbot.repository.CoinRepository;

@Service
public class CoinService {
    private final CoinRepository coinRepository;

    public CoinService(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    public Coin saveOrUpdateToDatabase(Coin coin) {
        return coinRepository.save(coin);
    }

    public Coin getCoinByName(String coinName) {
        return coinRepository.getCoinByCoinName(coinName);
    }
}
