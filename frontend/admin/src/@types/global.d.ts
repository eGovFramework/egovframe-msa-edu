interface Window {
  __localeId__: string
}
namespace NodeJS {
  interface Global {
    __localeId__: string
  }
}

declare module '*.png' {
  const resource: string
  export = resource
}
declare module '*.svg' {
  const resource: string
  export = resource
}
declare module '*.css' {
  const resource: any
  export = resource
}
declare module '*.pcss' {
  const resource: string
  export = resource
}
declare module '*.json' {
  const resource: any
  export = resource
}
