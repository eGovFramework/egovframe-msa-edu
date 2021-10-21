module.exports = {
  testPathIgnorePatterns: ['<rootDir>/.next/', '<rootDir>/node_modules/'],
  setupFilesAfterEnv: ['./jest.setup.ts'],
  moduleNameMapper: {
    '\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$':
      '<rootDir>/test/mocks.ts',
    '\\.(css|less|scss|html)$': '<rootDir>/test/mocks.ts',
    //절대 경로 세팅한 경우 jest에도 세팅이 필요함
    '^@pages(.*)$': '<rootDir>/pages$1',
  },
}
