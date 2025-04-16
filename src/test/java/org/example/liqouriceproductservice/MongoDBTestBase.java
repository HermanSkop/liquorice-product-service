package org.example.liqouriceproductservice;

import org.example.liqouriceproductservice.config.TestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Base class for tests that require MongoDB.
 * This class sets up the test environment with an embedded MongoDB instance.
 * Extend this class for any test that needs to interact with MongoDB.
 */
@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
public abstract class MongoDBTestBase {
}
