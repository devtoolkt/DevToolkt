# Updating correctness

Either the outer cell updates, or the current inner cell updates. Optionally, the outer cell, the current inner cell or
the updated cell update might freeze, but not in a way meeting the freezing condition (*).

The result cell should update. The updated value depends on a specific case.

The result cell should not freeze.

(*) The case when the result cell freezes (including a subcase when dependencies update) is handled by the _freezing
correctness_ tests
