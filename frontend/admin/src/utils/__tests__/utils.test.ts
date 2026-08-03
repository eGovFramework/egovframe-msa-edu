import { format, formatBytes, getType, Page, rownum, translateToLang } from '../index'

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
  })

  it('소수 자릿수를 지정할 수 있고 음수는 0자리로 처리한다', () => {
    expect(formatBytes(1536, 0)).toBe('2 KB')
    expect(formatBytes(1590, -1)).toBe('2 KB')
  })
})

describe('format', () => {
  it('{n} 자리를 인자로 치환한다', () => {
    expect(format('{0}자 이내로 입력하세요', [20])).toBe('20자 이내로 입력하세요')
    expect(format('{0} ~ {1}', ['가', '나'])).toBe('가 ~ 나')
  })

  it('대응하는 인자가 없으면 원래 표기를 남긴다', () => {
    expect(format('{0} {1}', ['가'])).toBe('가 {1}')
  })
})

describe('getType', () => {
  it('값의 실제 타입 이름을 반환한다', () => {
    expect(getType([])).toBe('Array')
    expect(getType({})).toBe('Object')
    expect(getType('a')).toBe('String')
    expect(getType(1)).toBe('Number')
    expect(getType(null)).toBe('Null')
    expect(getType(undefined)).toBe('Undefined')
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
