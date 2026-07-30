import { isValidEmail } from '@utils'

describe('isValidEmail', () => {
  describe('정상 이메일은 통과한다', () => {
    const validEmails = [
      'user@example.com',
      'user@example.io',
      'user.name@example.co.kr',
      'user-name@sub.example.com',
      // 6자를 초과하는 신규 일반 최상위 도메인(gTLD)
      'user@example.technology',
      'user@example.engineering',
      'user@example.international',
    ]

    validEmails.forEach(email => {
      it(`${email}`, () => {
        expect(isValidEmail(email)).toBe(true)
      })
    })
  })

  describe('비정상 이메일은 거부한다', () => {
    const invalidEmails = [
      '',
      'user',
      'user@',
      'user@example',
      '@example.com',
      // 최상위 도메인이 1자
      'a@b.c',
      'user example.com',
    ]

    invalidEmails.forEach(email => {
      it(`${email || '(빈 문자열)'}`, () => {
        expect(isValidEmail(email)).toBe(false)
      })
    })
  })
})
