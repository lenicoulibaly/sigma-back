package lenicorp.metier.payment.model.mappers;

import lenicorp.metier.payment.model.dtos.PaymentDTO;
import lenicorp.metier.payment.model.entities.Payment;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentMode", source = "paymentModeCode", qualifiedByName = "mapToType")
    @Mapping(target = "paymentType", source = "paymentTypeCode", qualifiedByName = "mapToType")
    Payment mapToEntity(PaymentDTO dto);

    @Mapping(target = "paymentModeCode", source = "paymentMode.code")
    @Mapping(target = "paymentModeName", source = "paymentMode.name")
    @Mapping(target = "paymentTypeCode", source = "paymentType.code")
    @Mapping(target = "paymentTypeName", source = "paymentType.name")
    PaymentDTO mapToDto(Payment entity);

    @Mapping(target = "paymentMode", source = "paymentModeCode", qualifiedByName = "mapToType")
    @Mapping(target = "paymentType", source = "paymentTypeCode", qualifiedByName = "mapToType")
    @Mapping(target = "paymentId", ignore = true)
    void updateEntity(PaymentDTO dto, @MappingTarget Payment entity);

    @Named("mapToType")
    default Type mapToType(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        return new Type(code.toUpperCase().trim());
    }
}
