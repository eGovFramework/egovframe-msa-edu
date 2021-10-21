import { IMainBanner, IMainBoard } from '@service'

export * from './MainLG'
export * from './MainSM'

export interface MainProps {
  banners: IMainBanner | null
  boards: IMainBoard | null
}

//banner type (메인, 하단, 협력기업)
export const bannerTypeCodes = ['0001', '0002', '0003']
export const slideCount = 3 // 0: 전부
// 게시판 - 공지사항, 자료실, 묻고답하기, 자주묻는질문 순서
export const boardNos = [1, 2, 3, 4]
