import { escapeHtml, escapeHtmlNl, nl2Br } from './index'

describe('escapeHtmlNl', () => {
  it('converts closing p/div tags to newlines', () => {
    expect(escapeHtmlNl('<p>a</p><div>b</div>')).toBe('a\nb\n')
  })

  it('handles closing tags case-insensitively', () => {
    expect(escapeHtmlNl('<P>a</P><DIV>b</DIV>')).toBe('a\nb\n')
  })

  it('strips other tags without inserting a newline', () => {
    // <i>/<span> 같은 태그는 제거만 하고 줄바꿈을 넣지 않아야 한다.
    expect(escapeHtmlNl('x<i>italic</i>y')).toBe('xitalicy')
    expect(escapeHtmlNl('m<span>s</span>n')).toBe('msn')
  })

  it('does not treat tags built from p/d/i/v letters as paragraph breaks', () => {
    // 정규식 문자클래스 버그([p|div]) 회귀 방지: </v>, </dip> 등은 줄바꿈이 아니다.
    expect(escapeHtmlNl('a</v>b')).toBe('ab')
    expect(escapeHtmlNl('a</dip>b')).toBe('ab')
  })

  it('removes &nbsp; entities', () => {
    expect(escapeHtmlNl('a&nbsp;b')).toBe('ab')
  })
})

describe('escapeHtml', () => {
  it('removes all html tags', () => {
    expect(escapeHtml('<p>hello <b>world</b></p>')).toBe('hello world')
  })
})

describe('nl2Br', () => {
  it('converts newlines to <br /> tags', () => {
    expect(nl2Br('a\nb')).toBe('a<br />b')
    expect(nl2Br('a\r\nb')).toBe('a<br />b')
  })
})
