package tech.qvanphong.tllnbot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "coin")
public class Coin {

    @Id
    private String coinName;

    private double usdPrice;

    private Timestamp time;

    public Coin() {
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public double getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(double usdPrice) {
        this.usdPrice = usdPrice;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Coin{" +
                "coinName='" + coinName + '\'' +
                ", usdPrice=" + usdPrice +
                ", time=" + time +
                '}';
    }
}
