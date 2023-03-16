package com.mastercard.fraud.model.transactionPost;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardNum {
    private String title;
    private String type;
    private BigInteger minimum;
    private BigInteger maximum;
    private List<BigInteger> examples;
    @JsonProperty("default")
    private BigInteger defaultAmount;


    @Override
    public String toString() {
        String var10000 = this.getTitle();

        List<BigInteger> valueList = this.getExamples();
        StringBuilder result = new StringBuilder("");

        for(BigInteger value: valueList) {
            StringBuilder builder = new StringBuilder(value.toString());
            builder.replace(4, 12, "********");
            result.append(builder);
        }


        return "CardNum(title=" + var10000 + ", type=" + this.getType() + ", minimum=" + this.getMinimum() + ", maximum=" + this.getMaximum() + ", examples=" + result + ", defaultAmount=" + this.getDefaultAmount() + ")";
    }

}
