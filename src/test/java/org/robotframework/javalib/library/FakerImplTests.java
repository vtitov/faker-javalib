package org.robotframework.javalib.library;


import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.library.faker.Logging;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import static org.robotframework.javalib.library.faker.Logging.*;


class FakerImplTests {

    final static int METHODS_TOTAL = 299;

    static {
        //Logging.setLogLevel(Logging.LogLevel.ALL);
        Logging.setLogLevel(Logging.LogLevel.INFO);
    }

    static private KeywordFactoryBasedLibrary<Keyword> robotFaker;


    @BeforeAll
    static void suiteSetup() {
        robotFaker = new FakerImpl();
    }


    @Test
    void helloClassGraph() throws Exception {
        assertThat(robotFaker.getKeywordNames().length, equalTo(METHODS_TOTAL));
    }

    @Test
    void parameterlessKeywords() throws Exception {
        assertThat("robotFaker", robotFaker, notNullValue(KeywordFactoryBasedLibrary.class));
        assertThat("empty list of keywords",robotFaker.getKeywordNames().length>0);

        val parameterizedKeywords = Stream.of(
            "Address County By Zip Code",
            "Address Zip Code By State",
            "Date Between",
            "Date Future",
            "Date Past",
            "Lorem Fixed String",
            "Lorem Paragraphs",
            "Lorem Sentences",
            "Number Digits",
            "Number Number Between",
            "Number Random Double",
            "Options Next Element",
            "Options Option",
            "NONPRESENTKEYWORD"
        ).flatMap(w->Stream.of(w, w.replaceAll(" ", "")))
            .collect(Collectors.toSet());

        val parameterlessKeywords = Arrays.stream(robotFaker.getKeywordNames())
            .filter(kw->!parameterizedKeywords.contains(kw))
            .collect(Collectors.toList());
        assertThat(parameterlessKeywords, not(empty()));

        parameterlessKeywords.forEach(kw->{
            trace("runKeyword: %s", kw);
            assertThat(kw, notNullValue(String.class));
            Optional.ofNullable(robotFaker.runKeyword(kw, new String[0]))
                .map(result -> {
                    trace("%s: %s", kw, result);
                    assertThat("no run result for keyword" + kw, result, notNullValue());
                    assertThat("empty result for keyword" + kw, result.toString(), not(emptyString()));
                    return result;
                })
                .orElseThrow(()->new AssertionError(kw));
        });
    }

    @Test
    void weatherDescription() throws Exception {
        val weatherDescription = robotFaker.runKeyword("Weather Description", new String[0]);
        trace("WeatherDescription: " + weatherDescription);
        assertThat("no weatherDescription",weatherDescription.toString().length()>0);
    }

    @Test
    void numberDigits() throws Exception {
        val numberDigits = robotFaker.runKeyword("Number Digits", Collections.singletonList(Integer.parseInt("7")).toArray(new Integer[0]));
        trace("Number Digits: " + numberDigits );
        assertThat("no weatherDescription",numberDigits .toString().length() == 7 );
    }


}
