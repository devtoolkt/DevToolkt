# Not freezing correctness

Some of the source cells freeze, leaving some of the other source cells warm. No source cell updates (*).

The result cell should not update.

The result cell should not freeze.

(*) The case when the result cell freezes (including a subcase when some source cells update) is handled by the
_freezing correctness_ tests
