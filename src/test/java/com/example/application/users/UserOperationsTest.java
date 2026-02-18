package com.example.application.users;

import com.example.application.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
public class UserOperationsTest {





}
