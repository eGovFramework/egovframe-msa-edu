import {
  format,
  formatBytes,
  getTextLength,
  isValidPassword,
  rownum,
  truncateText,
  translateToLang,
} from '../index'
import { Page } from '@service'

const page = (overrides: Partial<Page> = {}): Page =>
  ({
    size: 10,
    number: 0,
    totalElements: 35,
    ...overrides,
  } as Page)

describe('rownum', () => {
  it('오름차순은 페이지 시작 번호에 행 순서를 더한다', () => {
    expect(rownum(page(), 0, 'asc')).toBe(1)
    expect(rownum(page({ number: 2 }), 3, 'asc')).toBe(24)
  })

  it('기본값은 내림차순이며 전체 건수에서 역순으로 매긴다', () => {
    expect(rownum(page(), 0)).toBe(35)
    expect(rownum(page(), 9)).toBe(26)
    expect(rownum(page({ number: 3 }), 4)).toBe(1)
  })
})

describe('formatBytes', () => {
  it('1024 단위로 올려 표기한다', () => {
    expect(formatBytes(0)).toBe('0 Bytes')
    expect(formatBytes(512)).toBe('512 Bytes')
    expect(formatBytes(1024)).toBe('1 KB')
    expect(formatBytes(1536)).toBe('1.5 KB')
    expect(formatBytes(1048576)).toBe('1 MB')
    expect(formatBytes(1073741824)).toBe('1 GB')
  })

  it('소수 자릿수를 지정할 수 있고 음수는 0자리로 처리한다', () => {
    expect(formatBytes(1536, 0)).toBe('2 KB')
    expect(formatBytes(1536, 3)).toBe('1.5 KB')
    expect(formatBytes(1590, -1)).toBe('2 KB')
  })
})

describe('getTextLength', () => {
  it('byte 모드는 한글을 2로 센다', () => {
    expect(getTextLength('abc')).toBe(3)
    expect(getTextLength('한글')).toBe(4)
    expect(getTextLength('a한b')).toBe(4)
  })

  it('char 모드는 문자 수만 센다', () => {
    expect(getTextLength('한글', 'char')).toBe(2)
    expect(getTextLength('a한b', 'char')).toBe(3)
  })

  it('빈 문자열은 0이다', () => {
    expect(getTextLength('')).toBe(0)
    expect(getTextLength('', 'char')).toBe(0)
  })
})

describe('truncateText', () => {
  it('truncates ASCII text at the byte limit', () => {
    expect(truncateText('abcdef', 4)).toBe('abcd')
  })

  it('truncates Korean text using its weighted length', () => {
    expect(truncateText('한글텍스트', 5)).toBe('한글')
  })

  it('does not split a Unicode surrogate pair', () => {
    expect(truncateText('a😀b', 2)).toBe('a😀')
  })
})

describe('format', () => {
  it('{n} 자리를 인자로 치환한다', () => {
    expect(format('{0}자 이내로 입력하세요', [20])).toBe('20자 이내로 입력하세요')
    expect(format('{0} ~ {1}', ['가', '나'])).toBe('가 ~ 나')
  })

  it('같은 자리를 여러 번 써도 모두 치환한다', () => {
    expect(format('{0}/{0}', ['x'])).toBe('x/x')
  })

  it('대응하는 인자가 없으면 원래 표기를 남긴다', () => {
    expect(format('{0} {1}', ['가'])).toBe('가 {1}')
    expect(format('{0}', [])).toBe('{0}')
  })
})

describe('translateToLang', () => {
  const data = { korName: '한글명', engName: 'English' }

  it('ko는 한글 키를, 그 외 언어는 영문 키를 반환한다', () => {
    expect(translateToLang('ko', data)).toBe('한글명')
    expect(translateToLang('en', data)).toBe('English')
  })

  it('키 이름을 직접 지정할 수 있다', () => {
    const site = { siteKoName: '사이트', siteEnName: 'Site' }
    expect(translateToLang('ko', site, 'siteKoName', 'siteEnName')).toBe('사이트')
    expect(translateToLang('en', site, 'siteKoName', 'siteEnName')).toBe('Site')
  })
})

describe('isValidPassword', () => {
  it('영문·숫자·특수문자를 모두 포함한 8~20자를 허용한다', () => {
    expect(isValidPassword('abcd1234!')).toBe(true)
    expect(isValidPassword('Ab1!Ab1!')).toBe(true)
  })

  it('구성 요소가 빠지거나 길이를 벗어나면 거부한다', () => {
    expect(isValidPassword('abcdefgh')).toBe(false)
    expect(isValidPassword('abcd1234')).toBe(false)
    expect(isValidPassword('ab1!')).toBe(false)
    expect(isValidPassword('a1!' + 'b'.repeat(18))).toBe(false)
  })
})
