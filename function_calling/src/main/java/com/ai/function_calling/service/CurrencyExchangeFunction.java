package com.ai.function_calling.service;

import com.ai.function_calling.model.CurrencyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Configuration
public class CurrencyExchangeFunction {
    @Bean
    @Description("Quy đổi tỷ giá tiền tệ thực tế giữa các quốc gia như USD, EUR, VND")
    public Function<CurrencyModel.Request, CurrencyModel.Response> exchangeCurrency() {
        return request -> {
            log.info("🎯 Java Tool [exchangeCurrency] được gọi bởi AI với tham số: {} từ {} sang {}", request.amount(), request.from(), request.to());
            double rate = 1.0;
            String from = request.from().toUpperCase();
            String to = request.to().toUpperCase();
            // Mock tỷ giá đơn giản để demo
            if (from.equals("USD") && to.equals("VND")) rate = 25450.0;
            else if (from.equals("EUR") && to.equals("VND")) rate = 27200.0;
            else if (from.equals("VND") && to.equals("USD")) rate = 1.0 / 25450.0;
            double result = request.amount() * rate;
            String msg = String.format("Đã chuyển đổi thành công %s %s thành %s %s với tỷ giá %s",
                    request.amount(), from, result, to, rate);
            return new CurrencyModel.Response(result, msg);
        };
    }
}
