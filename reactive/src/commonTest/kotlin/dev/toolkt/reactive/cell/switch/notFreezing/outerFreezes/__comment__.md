# The outer cell freezes

The outer cell freezes (without updating). The current inner cell is warm and doesn't update (*).

The result cell should not update.

The result cell should not freeze.

(*) The case when the outer cell freezes and the current inner cell updates is handled by the _updating correctness_
tests.
