# 작업지침

- 한국어 사용
- 일반적인 작업 흐름: `/grill-with-docs` → `/to-spec` → `/to-tickets` → `/implement`
  - `/implement`는 내부적으로 `/tdd`로 구현하고, 커밋 전에 `/code-review`를 수행
- 버그 발생 시 `/diagnosing-bugs`, 아키텍처 개선이 필요할 때만 `/improve-codebase-architecture`
- 최대한 mattpocock 스킬들을 활용

## Agent skills

### Issue tracker

Issues are tracked in GitHub Issues for `zoek1211/rest-api` via the `gh` CLI. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: one `GLOSSARY.md` and `docs/adr/` at the repo root. See `docs/agents/domain.md`.
