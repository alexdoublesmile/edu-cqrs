package edu.joyful.userservice.model;

import edu.joyful.commonservice.api.payment.dto.CardDetails;
import edu.joyful.commonservice.api.user.dto.UserDto;
import edu.joyful.commonservice.api.user.query.GetUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserProjection {

    @QueryHandler
    public UserDto getUserPaymentDetails(GetUserPaymentDetailsQuery query) {
        final CardDetails card = CardDetails.builder()
                .cardNumber("123456")
                .name("Alex")
                .validUntilMonth(01)
                .validUntilYear(2022)
                .cvv(111)
                .build();

        UserDto.builder()
                .userId(query.getUserId())
                .firstName("Alex")
                .lastName("Smith")
                .cardDetails(card)
                .build();
    }
}
