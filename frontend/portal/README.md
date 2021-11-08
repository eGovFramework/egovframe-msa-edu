# Frontend Boilerplate

Next.js + Typescript 활용한 React 기반 프론트엔드 기본 설정 Boilerplate.
해당 프로젝트에 설정된 부분은 [여기를](https://www.notion.so/Frontend-Boilerplate-b4f07b67713243f1bb0050cd35970bc9) 확인!!

### Tech stack

- [Next.js](https://nextjs.org/docs/getting-started)
- [Typescript](https://www.typescriptlang.org/docs/)
- [ESLint](https://eslint.org/) + [Prettier](https://prettier.io/) + [airbnb Style Guide](https://github.com/airbnb/javascript)
- [Jest](https://jestjs.io/docs/next/getting-started) + [testing-library](https://testing-library.com/docs/)

## Getting Started

First, run the development server:

```bash
npm install

# 기본 소규모 모드
npm run dev
# or
yarn dev

# 소규모 모드 for Windows
npm run dev:smWin
# 소규모 모드 for others
npm run dev:sm

# 대규모 모드 for Windows
npm run dev:lgWin
# 대규모 모드 for others
npm run dev:lg
```

```bash
├─public                 # static resource root
│  ├─locales             # 다국어 message.json
│  └─styles				 # css + images
│
├─server                 # custom server
│  └─index.ts
│
├─src                   # source root
│   ├─@types                # type declaration
│   ├─components            # components
│   ├─constants             # 상수
│   ├─hooks                 # custom hooks
│   ├─libs                  # deps library custom
│   ├─pages                 # next.js page routing
│   │   ├─api               # next.js api routing
│   │   └─auth              # 로그인 관련
│   ├─service               # API 호출
│   ├─stores                # recoil 상태관리
│   ├─styles                # material ui theme 관리
│   └─utils                 # utils
│
├─test                      # test 관련
│
│ .babelrc              # babel config
│ .dockerignore         # docker ignore
│ .env.local            # environment variables
│ .eslintrc.js          # eslint config
│ .gitignore            # git ignore
│ .prettierrc.js        # prettier config
│ Dockerfile            # Docker 배포 시
│ jest.config.js        # jest config
│ jest.setup.ts         # jest에서 testing-library 사용하기 위한 설정(그외 jest에 필요한 외부 라이브러리 설정)
│ manifest.yml          # cf 배포 시
│ next-env.d.ts         # next.js type declare
│ next.config.js        # next.js 설정
│ package.json.
│ README.md
│ tsconfig.json          # typescirpt config
└ tsconfig.server.json   # custom server 사용 시 typescript config
```
