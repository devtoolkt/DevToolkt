# Not freezing correctness

Either the outer cell, the current inner cell or the updated inner cell freeze in a way that doesn't meet the freezing
condition. Neither the outer cell nor the current cell update (*).

The result cell should not update.

The result cell should not freeze.

(*) The case when the result cell freezes (including a subcase when dependencies update) is handled by the _freezing
correctness_ tests
