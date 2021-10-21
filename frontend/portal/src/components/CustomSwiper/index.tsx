import React from 'react'
import 'swiper/components/pagination/pagination.min.css'
import SwiperCore, { Autoplay, Pagination } from 'swiper/core'
import { Swiper } from 'swiper/react'
// Import Swiper styles
import 'swiper/swiper.min.css'

// install Swiper modules
SwiperCore.use([Pagination, Autoplay])

interface CustomSwiperProps extends Swiper {
  children: React.ReactNode[]
}

const CustomSwiper = (props: CustomSwiperProps) => {
  const { children, ...rest } = props
  return <Swiper {...rest}>{children && children.map(item => item)}</Swiper>
}

export default CustomSwiper
