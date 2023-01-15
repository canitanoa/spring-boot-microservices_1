package com.microservices.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.productservice.dto.ProductRequest;
import com.microservices.productservice.repository.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers //Para que sea compatible con Testcontainers
@AutoConfigureMockMvc //Para MockMvc
class ProductServiceApplicationTests {

	//Para implementar las peticiones http
	@Autowired
	private MockMvc mockMvc;

	//Para mapear de Json a String
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	//Definimos el contenedor de MongoDB
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	//Definimos las propiedades para configurar el acceso a la DB
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dinDynamicPropertyRegistry){
		//seteamos la uri con una url dinamica obtenida del contenedor
		dinDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	//Test de integración para el POST api/product
	@Test
	void shoulCreateProduct() throws Exception {

		//Seteamos el request
		ProductRequest productRequest = getProductRequest();
		//Mapeamos el request a string
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		//Hacemos el POST
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
					.contentType(MediaType.APPLICATION_JSON)
					.content(productRequestString) //Pasamos el objeto request
				)
				.andDo(print())
				.andExpect(status().isCreated());

		//Validamos
		Assertions.assertTrue(productRepository.findAll().size() == 1);
	}

	//Metodo que setea el objeto del request
	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Phone")
				.description("Telefono smartphone")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

	//Test de integración para el GET api/product
	@Test
	public void getAllProductsAPI() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
						.get("/api/product")
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", Matchers.is("Phone")));
	}





}
