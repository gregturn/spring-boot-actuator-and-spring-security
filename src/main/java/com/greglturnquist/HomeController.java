/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greglturnquist;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Greg Turnquist
 */
@Controller
public class HomeController {

	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	private final ResourceLoader resourceLoader;

	@Autowired
	public HomeController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@RequestMapping(value = "/")
	public String index(Model model, @AuthenticationPrincipal Object auth) throws IOException {
		log.info(currentUser(auth) + " is looking up all images");

		return "index";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<?> pathVariablizedImage(@PathVariable String filename, @AuthenticationPrincipal Object auth) {
		log.info(currentUser(auth) + " is looking up a @PathVariable-based raw image");

		return getRawImage();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/image")
	@ResponseBody
	public ResponseEntity<?> fixedImage(@AuthenticationPrincipal Object auth) {
		log.info(currentUser(auth) + " is looking up a fixed raw image");

		return getRawImage();
	}

	private ResponseEntity<?> getRawImage() {
		try {
			Resource file = resourceLoader.getResource("file:upload-dir/keep-calm-and-learn-javascript.jpg");
			return ResponseEntity.ok()
					.contentLength(file.contentLength())
					.contentType(MediaType.IMAGE_JPEG)
					.body(new InputStreamResource(file.getInputStream()));
		} catch (IOException e) {
			return ResponseEntity.badRequest()
					.body("Couldn't find it => " + e.getMessage());
		}
	}

	private static String currentUser(Object auth) {
		return Optional.ofNullable(auth)
				.map(authentication -> authentication.toString())
				.orElse("Unknown user");
	}


}
