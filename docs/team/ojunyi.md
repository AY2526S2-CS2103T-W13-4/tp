---
layout: page
title: Ong Jun Yi's Project Portfolio Page
---

### Project: CoursePilot

CoursePilot is a desktop app for university tutors and teaching assistants (TAs) who manage one or more tutorial groups. It is optimized for use via a Command Line Interface (CLI) while retaining the benefits of a Graphical User Interface (GUI). It is written in Java and has about 15 kLoC.

Given below are my contributions to the project.
---

### Code Contribution in RepoSense

* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=ojunyi&tabRepo=AY2526S2-CS2103T-W13-4%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

---

### Enhancements Implemented Summary

* **New Feature**: Added the `select` command.
  * What it does: Allows the tutor to set a tutorial as the **current operating tutorial**, scoping all subsequent student-level commands (`add -student`, `delete -student`, `list -student`, `find`) to that tutorial. The tutor can also use `select none` to clear the selection.
  * Justification: The `select` command is the backbone of CoursePilot's tutorial-scoped workflow. Without it, there is no way to distinguish which tutorial a student operation should apply to, especially when a tutor manages multiple tutorial groups.
  * Highlights: Required adding `getTutorialByCode()`, `setCurrentOperatingTutorial()`, `clearCurrentOperatingTutorial()`, `getCurrentOperatingTutorial()`, and `getCurrentOperatingTutorialProperty()` to both the `Model` interface and `ModelManager`. The current operating tutorial is exposed as a JavaFX `ObjectProperty<Tutorial>` to enable reactive UI binding across the status bar and tutorial panel highlights.

* **New Feature**: Modified and enhanced the Command Autocomplete feature.
  * What it does: Provides context-aware suggestions as the user types in the command box. Suggestions appear in a popup dropdown above the input field and cover command words, mode flags (`-student`, `-tutorial`), and field prefixes (`/name`, `/phone`, `/email`, `/matric`, `/tag`). Already-used prefixes are excluded from suggestions, except `/tag` which can be repeated.
  * Justification: This feature significantly improves usability for new tutors who may not have memorised the full command syntax, while also speeding up input for experienced users through Tab completion.
  * Highlights: The autocomplete logic is implemented in a separate `CommandAutoCompleter` class in the Logic layer to maintain separation of concerns and allow independent unit testing. The UI integration required redesigning `CommandBox` to manage a JavaFX `Popup` with keyboard navigation (Tab, Enter, Escape, Up, Down) and click support. Implemented `NoOpListSelectionModel` and `NoOpTableSelectionModel` to suppress unwanted JavaFX selection behaviour in the UI panels.

* **New Feature**: Refactored and enhanced the GUI (`TutorialCodeListPanel` and `TutorialDetailsPanel`).
  * What it does: Displays two synchronized table panels - one showing tutorial codes and one showing corresponding details (day, time slot, capacity). The currently selected tutorial is highlighted in the code panel.
  * Justification: Tutors need to see all their tutorials and their details at a glance without running a separate command.
  * Highlights: Required synchronizing the vertical scroll bars of two independent `TableView` components so that scrolling one scrolls the other. `SplitPane` dividers are locked to prevent manual resizing. Custom cell rendering is used to highlight the current operating tutorial with a visual indicator.

* **Enhancement to Existing Feature**: Extended the `find` command to support field-specific search.
  * What it does: Extends the original name-only `find` command with three additional search modes using field flags - `/phone` (prefix match), `/email` (substring match), `/matric` (prefix match, case-insensitive).
  * Justification: Tutors frequently need to look up students by matric number or email, not just name. The extended `find` command makes CoursePilot significantly more practical for real tutorial management workflows.
  * Highlights: Redesigned `FindCommand` to accept a generic `Predicate<Student>` instead of a fixed name predicate. Added `FindCommand.Flag` enum with `fromString()` and `validFlagsString()` utility methods. Implemented three new predicate classes and refactored all four predicate classes (including the original `NameContainsKeywordsPredicate`) to extend a new abstract base class `StudentFieldPredicate`, eliminating significant structural duplication. Added `isStudentInCurrentTutorial()` to `Model` to support tutorial-scoped search without violating the Law of Demeter. Fixed a case-sensitivity bug in `StringUtil.containsPartWordIgnoreCase()` where `String.contains()` was used without case normalization.

* **Enhancement to Existing Feature**: Extended `StudentCard` and `StudentListPanel` to display tutorial tags on each student card.
  * What it does: Each student card now shows the tutorial groups the student is enrolled in as visual label tags.
  * Justification: Tutors managing students across multiple tutorials need to see at a glance which tutorials a student belongs to, especially when viewing the global student list.
  * Highlights: The tutorial list is passed as a single source of truth from `StudentListPanel` down to each `StudentCard` and filtered at render time, avoiding redundant per-student storage of tutorial membership data.

* **Enhancements to existing features**: General changes.
  * Refactored all predicate classes to extend `StudentFieldPredicate`, removing structural duplication across `NameContainsKeywordsPredicate`, `EmailContainsKeywordsPredicate`, `PhoneStartsWithKeywordsPredicate`, and `MatricNumberStartsWithKeywordsPredicate`
  * Fixed `equals`/`hashCode` contract violation in `SelectCommand` and predicate classes
  * Fixed Law of Demeter violations in `SelectCommand` and `FindCommand` by adding appropriate methods to the `Model` interface
  * Fixed case-sensitivity bug in `StringUtil.containsPartWordIgnoreCase()`
  * Standardised Javadoc, error messages, and coding conventions across `StringUtil`, `AddCommand`, `EditCommand`, `DeleteCommand`, `ListCommand`, `FindCommand`, and `SelectCommand`
  * Wrote automated tests for `EmailContainsKeywordsPredicate`, `PhoneStartsWithKeywordsPredicate`, and `MatricNumberStartsWithKeywordsPredicate`, mirroring the structure of the existing `NameContainsKeywordsPredicateTest`

* **Documentation**:
  * User Guide:
    * Wrote the "Who is CoursePilot for?" section establishing the target user profile, value proposition, and user assumptions
    * Wrote the full `select` command section including format, behaviour notes, and examples with expected outputs
    * Wrote the full `find` command section documenting all four search modes with examples and expected outputs
    * Wrote the Command Autocomplete section documenting keyboard shortcuts and suggestion behaviour
    * Standardised command format terminology (Modes vs Prefixes) across all command sections
    * Added expected output messages to examples across `select`, `list`, `add`, `find`, `delete`, and `clear` sections
    * Captured, annotated, and integrated all screenshots in the User Guide, ensuring each screenshot accurately reflects the current product UI
    * Updated all command examples to match the annotated screenshots for consistency
    * Identified and filled in missing content that teammates had omitted, ensuring full coverage of all commands and edge cases
    * Updated the majority of the Known Issues section to reflect accurately observed product limitations
  * Developer Guide:
    * Contributed the majority of User Stories, including adding more specific tutor personas and rewriting use cases to leave out UI elements
    * Wrote and rewrote all Use Cases (UC01–UC10) to correct MSS structure, fix stale descriptions, add missing use cases for `select`, `find`, and `clear`, and align with actual product behaviour
    * Rewrote Non-Functional Requirements reorganised by category with added NFRs for response time, startup time, data integrity, and usability
    * Rewrote Glossary to remove unimplemented terms, add missing terms, and standardise formatting
    * Updated Value Proposition in product scope to match implemented features
    * Updated all GitHub links throughout the DG to point to the correct CoursePilot repository instead of the original AB3 repository
    * Added manual testing instructions for adding a student, adding a tutorial, and deleting a tutorial
    * Added and/or updated the following UML diagrams:
      * `AutocompleteActivityDiagram.puml`
      * `AutocompleteCommandActivityDiagram.puml`
      * `ArchitectureSequenceDiagram.puml`
      * `DeleteSequenceDiagram.puml`
      * `ModelClassDiagram.puml`
      * `UiClassDiagram.puml`
      * `StorageClassDiagram.puml`

* **Contributions to team-based tasks**:
  * Maintained the issue tracker throughout the project by defining, assigning, and tracking tasks across all milestones
  * Contributed to renaming the product from AddressBook to CoursePilot, including updating class names, message strings, and documentation references
  * Updated several areas in the user and developer documents that are not clear enough
  * Conducted comprehensive alpha testing of the entire product, identifying the majority of internal alpha bugs including input validation gaps, case-sensitivity issues, and missing error handling across multiple commands. Additionally, identified several other product issues beyond my own features and shared them with teammates to support the team's overall QA coverage.
  * Reviewed and corrected teammates' implementations for correctness, identifying bugs that were initially dismissed but later resurfaced during external PE testing

* **Review/mentoring contributions and contributions beyond the project team**:
  * Prior to Week 8, most code review feedback was provided as inline comments directly on GitHub. The PR list below reflects reviews with non-trivial written feedback; additional reviews were conducted by pulling and manually testing the code locally. There were several other PRs where I documented the testing steps taken to verify a teammate's implementation. These were intentionally excluded from the list above as they were considered part of routine review rather than non-trivial feedback.
  * PRs reviewed (with non-trivial review comments): [\96](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/96), [\91](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/91), [\210](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/210), [\198](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/198), [\189](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/189), [\90](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/90), [\87](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/87), [\41](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/41), [\142](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/142), [\196](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/196), [\281](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/281), [\286](https://github.com/AY2526S2-CS2103T-W13-4/tp/pull/286)
  * Link to [PED](https://github.com/NUS-CS2103-AY2526-S2/ped-ojunyi/issues) repository provided by NUS CS2103T Team.
  * Reported bugs and suggestions for other teams during PED: [1](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/239), [2](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/235), [3](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/234), [4](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/233), [5](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/232), [6](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/231), [7](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/230), [8](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/229), [9](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/226), [10](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/225), [11](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/223), [12](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/222), [13](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/220), [14](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/218), [15](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/216), [16](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/215), [17](https://github.com/AY2526S2-CS2103T-T12-4/tp/issues/214).
