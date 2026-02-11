package com.qburst.microservice.auth.dto.response.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // include only non-null values in api response
@JsonIgnoreProperties(ignoreUnknown = true) // ignore unknown properties
@JsonPropertyOrder({"id", "username", "email", "roles", "firstname", "lastname"})
public class UserResponse {

    private Long id;

    @Schema(example = "brij.kishor") // shows in swagger as sample
    @JsonProperty(value = "username", required = true)
    private String username;

    @Schema(example = "USER")
    @JsonProperty(value = "roles", required = true)
    private String roles;

    @Schema(example = "Brij")
    @JsonProperty("firstname")
    private String firstname;

    @Schema(example = "Kishor")
    @JsonProperty("lastname")
    private String lastname;

    @Schema(example = "brij.kishor@gmail.com")
    @JsonProperty(value = "email", required = true)
    private String email;
}
