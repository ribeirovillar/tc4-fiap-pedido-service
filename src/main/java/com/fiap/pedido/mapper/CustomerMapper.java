package com.fiap.pedido.mapper;

import com.fiap.pedido.domain.Customer;
import com.fiap.pedido.gateway.web.json.CustomerResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer map(CustomerResponseDTO customerResponseDTO);

}
