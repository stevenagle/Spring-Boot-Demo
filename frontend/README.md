# MockPortal frontend

React + TypeScript + Vite UI for the Spring Boot account demo.
See the [project README](../README.md) for setup, API contracts, and security scope.

```sh
npm ci
npm run dev
```

Vite proxies `/api` to the local backend. Optional `VITE_API_URL` overrides the complete
profile collection URL; see `.env.example`.

```sh
npm run lint
npm run format:check
npm test
npm run build
```

Use `npm run format` for formatting and `npm run test:watch` for interactive tests.
