import { Page } from '@service'

export const translateToLang = (
  cur: string,
  data: any,
  korKey: string = 'korName',
  otherKey: string = 'engName',
): string => {
  if (cur === 'ko') {
    return data[korKey]
  }

  return data[otherKey]
}

// DataGrid rownum 계산..
export const rownum = (data: Page, index: number, orderby?: 'asc' | 'desc') => {
  if (orderby === 'asc') {
    return data.size * data.number + index + 1
  }
  return data.totalElements - data.size * data.number - index
}

export const formatBytes = (bytes: number, decimals: number = 2) => {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const dec = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dec)) + ' ' + sizes[i]
}

export const getTextLength = (
  str: string,
  format: 'byte' | 'char' = 'byte',
) => {
  let len: number = 0
  for (const character of str) {
    if (format === 'byte' && escape(character).length === 6) {
      len++
    }
    len++
  }

  return len
}

// 텍스트 포멧
export const format = (text: string, args: any[]) =>
  text.replace(/{(\d+)}/g, (match, number) =>
    typeof args[number] !== 'undefined' ? args[number] : match,
  )

// 태그 제거
export const escapeHtml = html => {
  return html.replace(/(<([^>]+)>)/gi, '')
}

// 개행 태그(p, div) 제거
export const escapeHtmlNl = html => {
  return html.replace(/(<[/]([p|div]+)>)/gi, '\n').replace(/(<([^>]+)>)/gi, '').replace(/(\&nbsp\;)/gi, '')
}

// 개행 문자를 br 태그로 변환
export const nl2Br = html => {
  return html.replace(/(\r\n|\n\r|\r|\n)/g, '<br />')
}

// 비밀번호 형식 확인
export const isValidPassword = value => {
  return /^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,20}$/.test(value)
}
