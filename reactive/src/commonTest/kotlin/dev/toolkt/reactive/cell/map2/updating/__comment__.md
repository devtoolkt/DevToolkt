# Updating correctness

Some of the sources cells update. Optionally, one of the source cells might freeze, as long the other remains warm (*).

The result cell should update. The updated value should be the result of the applying the transformation function with
the new values of the respective source cells.

The result cell should not freeze.

(*) The case when both source cells freeze (causing the result cell to freeze) is handled by the _freezing correctness_
tests
