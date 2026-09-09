package com.example.junittesting;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class CalcTests {


    // One test should generally represent one behavior/scenario, while having as many assertions as make sense for that behavior.
    @Test
    void testAdd() {
        
        // arrange
        var calc = new Calculator();

        // act
        int result = calc.add(2, 2);

        // assert
        assertEquals(4, result);
        
        // act again
        result = calc.add(2, 0);

        // assert
        assertEquals(2, result);
    }

    // above is correct but use AAA
    // Arrange -> Act -> assert

    @Test
    void checkMultiply(){

        var calc = new Calculator();

        int result = calc.multiply(2, 2);
        assertEquals(4, result);
        
        result = calc.multiply(2, 3);
        assertEquals(6, result);


    }
}
