# Frontend Admin

Next.js + typescript + material ui 활용한 admin dashboard.

## Getting Started

First, run the development server:

```bash
npm install
npm run dev
```

run the local to production mode:

```bash
npm install
npm run build
npm run start
```

### 환경변수

- `.env.local.sample` 파일처럼 `.env.local` 파일을 생성하여 필요한 환경변수를 세팅한다.
- 사용하고 있는 환경변수는 `./src/constants/env.ts`, `./next.config.js` 파일을 확인한다.

## 폴더 구조

```bash
├─public                 # static resource root
│  └─images
│
├─server                # custom server
│  └─index.ts
│
├─src                   # source root
│   ├─@types                # type declaration
│   ├─components            # components
│   ├─constants             # constants
│   ├─hooks                 # custom hooks
│   ├─lib                   # deps library custom
│   ├─pages                 # next.js page routing
│   │   ├─api               # next.js api routing
│   │   └─auth              # 로그인 관련
│   ├─store                 # recoil 상태관리
│   └─styles                # global styles
│
├─test                      # test 관련
│
│ .babelrc              # babel config
│ .env.local            # environment variables
│ .eslintrc.js          # eslint config
│ .gitignore            # git ignore
│ .prettierrc.js        # prettier config
│ jest.config.js         # jest config
│ jest.setup.ts         # jest에서 testing-library 사용하기 위한 설정(그외 jest에 필요한 외부 라이브러리 설정)
│ next-env.d.ts         # next.js type declare
│ next.config.js         # next.js config
│ package.json
│ README.md
│ tsconfig.json          # typescirpt config
└ tsconfig.server.json   # custom server 사용 시 typescript config

```
