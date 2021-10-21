import {
  bannerTypeCodes,
  boardNos,
  MainLG,
  MainSM,
  slideCount,
} from '@components/Main'
import { MODE } from '@constants/env'
import {
  bannerService,
  boardService,
  IMainBanner,
  IMainBoard,
  IMainItem,
  reserveService,
} from '@service'
import { GetServerSideProps } from 'next'
import React from 'react'

export interface IHomeItemsProps {
  banners: IMainBanner | null
  boards: IMainBoard | null
  reserveItems: IMainItem | null
}

const Home = ({ banners, boards, reserveItems }: IHomeItemsProps) => {
  return (
    <>
      {MODE === 'sm' ? (
        <MainSM banners={banners} boards={boards} />
      ) : (
        <MainLG banners={banners} boards={boards} reserveItems={reserveItems} />
      )}
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async () => {
  let banners = {}
  let boards = {}
  let reserveItems = {}
  try {
    const banner = await bannerService.getBanners(bannerTypeCodes, slideCount)
    if (banner) {
      banners = (await banner.data) as IMainBanner
    }
    const result = await boardService.getMainPosts(boardNos, slideCount)
    if (result) {
      boards = (await result.data) as IMainBoard
    }

    if (MODE === 'lg') {
      const items = await reserveService.getMainItems(slideCount)
      if (items) {
        reserveItems = items.data as IMainItem
      }
    }
  } catch (error) {
    console.error(`posts item query error ${error.message}`)
  }

  return {
    props: {
      banners,
      boards,
      reserveItems,
    },
  }
}

export default Home
