# Frontend Admin Boilerplate

Next.js + typescript + material ui 활용한 admin dashboard Boilerplate.

[notion link](https://www.notion.so/Nextjs-MUI-Admin-template-bc57d86c94724bbf83601883c2d5ec13)

## Getting Started

First, run the development server:

```bash
npm install
npm run dev
# or
yarn
yarn dev
```

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
