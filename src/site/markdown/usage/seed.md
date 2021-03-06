# Seed

For each property parameter, junit-quickcheck uses a unique value as a seed
for the source of randomness used to generate the parameter's values. To fix
the seed value for a property parameter, use the `seed` attribute of the
`@When` annotation.

```java
    @RunWith(JUnitQuickcheck.class)
    public class SameValues {
        @Property public void holds(@When(seed = -1L) int i) {
            // ...
        }
    }
```

You may want to fix the seed when a property fails, so that you can test the
property over and over again with the same set of generated values that caused
the failure.

junit-quickcheck reports the seed used for a given property parameter in the
message of the `AssertionError` raised when a property fails.

    java.lang.AssertionError: Property named 'myProperty' failed:
    With arguments: [10]
    Original failure message:
    Expected: a value less than <1>
         but: <10> was greater than <1>
    First arguments found to also provoke a failure: [753701363]
    Seeds for reproduction: [-6700838609453830748]
    ...
