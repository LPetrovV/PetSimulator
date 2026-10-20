# Appendix B – Reflective Summary on AI Use (one page)

> **This must be written by you, in your own words** – the marking criteria
> say so explicitly. Delete everything in this file and replace it with your
> own reflection. The questions and facts below are only there to help you
> remember what happened.

## Questions to answer (aim for ~1 paragraph each)

1. **How did you use AI?**
   What did you ask it to do, and what did you write or decide yourself?
   (See the AI Interaction Log for the exact list.)

2. **How did it influence the design?**
   - It suggested keeping `SaveSystem`'s public methods the same so the rest of
     the game barely changed. Was that a good choice? Would you have done it
     differently?
   - The item list became its own `INVENTORY` table instead of one text
     column. Why is that better (or not)?
   - Plain JDBC was chosen over Hibernate. Do you agree?

3. **Critical thinking – where was the AI wrong, risky, or worth checking?**
   Ideas you can talk about, if they match your experience:
   - Did you check the SQL works in Derby (e.g. Derby has no `LIMIT`)?
   - Did you test it yourself – run the game, quit, restart, load?
   - Did you look at the database in NetBeans' Services tab to confirm the rows?
   - The AI found a bug in the existing code (pet and player names swapped in
     `GameController`) – how did you verify that and what did you do?
   - Anything you did not understand at first (transactions, `ON DELETE
     CASCADE`, the shutdown hook) and how you made sure you understood it
     before submitting.

4. **What did you learn, and what would you do differently next time?**
