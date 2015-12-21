/*
 The MIT License

 Copyright (c) 2010-2015 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.pholser.junit.quickcheck.runner;

import java.util.List;
import java.util.Random;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.GeneratorRepository;
import com.pholser.junit.quickcheck.internal.generator.ServiceLoaderGeneratorSource;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>JUnit test runner for junit-quickcheck property-based tests.</p>
 *
 * <p>When this runner runs a given test class, it regards only
 * {@code public} instance methods with a return type of {@code void} that are
 * marked with the {@link Property} annotation.</p>
 *
 * <p>This runner honors {@link Rule}, {@link Before}, {@link After},
 * {@link BeforeClass}, and {@link AfterClass}. Their execution is wrapped
 * around the verification of a property in the expected order.</p>
 */
public class JUnitQuickcheck extends BlockJUnit4ClassRunner {
    private final GeneratorRepository repo;
    private final GeometricDistribution distro;
    private final Logger seedLog;

    public JUnitQuickcheck(Class<?> clazz) throws InitializationError {
        super(clazz);

        SourceOfRandomness random = new SourceOfRandomness(new Random());
        repo = new GeneratorRepository(random).register(new ServiceLoaderGeneratorSource());
        distro = new GeometricDistribution();
        seedLog = LoggerFactory.getLogger("junit-quickcheck.seed-reporting");
    }

    @Override protected void validateTestMethods(List<Throwable> errors) {
        for (FrameworkMethod each : computeTestMethods())
            each.validatePublicVoid(false, errors);
    }

    @Override protected List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(Property.class);
    }

    @Override public Statement methodBlock(FrameworkMethod method) {
        return new PropertyStatement(method, getTestClass(), repo, distro, seedLog);
    }
}