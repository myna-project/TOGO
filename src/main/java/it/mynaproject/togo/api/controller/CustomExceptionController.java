/*******************************************************************************
 * Copyright (c) Myna-Project SRL <info@myna-project.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 * - Myna-Project SRL <info@myna-project.org> - initial API and implementation
 ******************************************************************************/
package it.mynaproject.togo.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.ForbiddenException;
import it.mynaproject.togo.api.exception.GenericException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.ErrorResponse;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;

@ControllerAdvice
public class CustomExceptionController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	/*
	 * 400 errors and not handled errors
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public <T extends Exception> ResponseEntity<ErrorResponse> handleException(T exception, HttpServletResponse response) {

		log.error(exception.getMessage(), exception);

		if (exception.getCause() instanceof UnrecognizedPropertyException || exception.getCause() instanceof ConversionFailedException || exception.getCause() instanceof ClassCastException) {
			return new ResponseEntity<>(new ErrorResponse(400, "Cannot perform this request!"), HttpStatus.BAD_REQUEST);
		} else if (exception.getCause() instanceof JsonParseException || exception.getCause() instanceof JsonMappingException) {
			return new ResponseEntity<>(new ErrorResponse(400, "JSON received is not valid!"), HttpStatus.BAD_REQUEST);
		} else if (exception instanceof MethodArgumentNotValidException) {
			List<String> errors = ((BindException) exception).getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.toList());
			return new ResponseEntity<>(new ErrorResponse(400, String.join(" - ", errors)), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(new ErrorResponse(500, "Error occurred!"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/*
	 * 403 errors
	 */
	@ExceptionHandler(ForbiddenException.class)
	@ResponseBody
	public <T extends ForbiddenException> ResponseEntity<ErrorResponse> handle403Exception(T exception) {

		log.error(exception.getMessage(), exception.getCause());

		return new ResponseEntity<>(new ErrorResponse(exception.getCode(), exception.getMessage()), HttpStatus.FORBIDDEN);
	}

	/*
	 * 404 errors
	 */
	@ExceptionHandler(NotFoundException.class)
	@ResponseBody
	public <T extends NotFoundException> ResponseEntity<ErrorResponse> handle404Exception(T exception) {

		log.error(exception.getMessage(), exception.getCause());

		return new ResponseEntity<>(new ErrorResponse(exception.getCode(), exception.getMessage()), HttpStatus.NOT_FOUND);
	}

	/*
	 * 409 errors
	 */
	@ExceptionHandler(ConflictException.class)
	@ResponseBody
	public <T extends ConflictException> ResponseEntity<ErrorResponse> handle409Exception(T exception) {

		log.error(exception.getMessage(), exception.getCause());

		return new ResponseEntity<>(new ErrorResponse(exception.getCode(), exception.getMessage()), HttpStatus.CONFLICT);
	}

	/*
	 * 500 errors handled
	 */
	@ExceptionHandler(GenericException.class)
	@ResponseBody
	public <T extends GenericException> ResponseEntity<ErrorResponse> handle500Exception(T exception) {

		log.error(exception.getMessage(), exception.getCause());

		return new ResponseEntity<>(new ErrorResponse(exception.getCode(), exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
