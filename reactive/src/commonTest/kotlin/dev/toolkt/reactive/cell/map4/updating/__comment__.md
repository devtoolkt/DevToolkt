# Updating correctness

Some of the sources cells update. Optionally, some of the source cells might freeze, as long as some warm source cells
are left (*).

The result cell should update. The updated value should be the result of the applying the transformation function with
the new values of the respective source cells.

The result cell should not freeze.

(*) The case when the result cell freezes (including a subcase when some source cells update) is handled by the
_freezing correctness_ tests
