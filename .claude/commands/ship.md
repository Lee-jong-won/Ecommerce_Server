Run all tests, and if they all pass, commit and push to the current branch.

## Steps

1. **Run tests** using `./gradlew test`
   - Stream the output so the user can see progress
   - If any test fails, stop immediately and report the failures — do NOT commit or push

2. **If all tests pass**, do the following:
   - Run `git status` to show what will be committed
   - Ask the user for a commit message (keep it concise, follow conventional commits style, e.g. `feat: ...`, `fix: ...`, `refactor: ...`)
   - Stage all changes: `git add -A`
   - Commit with the provided message
   - Push to the current branch: `git push origin <current-branch>`
   - Confirm success with the pushed commit hash

## Rules
- Never skip or bypass hooks (--no-verify is forbidden)
- Never force-push
- If the user does not provide a commit message, ask again — do not generate one silently without confirmation
