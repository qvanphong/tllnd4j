package tech.qvanphong.tllnbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.qvanphong.tllnbot.entity.Coin;

@Repository
public interface CoinRepository extends JpaRepository<Coin, String> {
    @Override
    <S extends Coin> S save(S coin);

    Coin getCoinByCoinName(String coinName);
}
