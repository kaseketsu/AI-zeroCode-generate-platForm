import dayjs from 'dayjs'

/**
 * 根据制定格式返回时间
 * @param time 时间
 * @param format 格式
 */
export const formatTime = (time: string | undefined, format: string = 'YYYY-MM-DD HH:mm:ss'): string => {
  if (!time) return ''
  return dayjs(time).format(format)
}
