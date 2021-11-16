function formatDate(date, fmt) {
  function _fmt(date, fmt) {
    let ret
    const opt = {
      'Y+': date.getFullYear().toString(), // 年
      'M+': (date.getMonth() + 1).toString(), // 月
      'd+': date.getDate().toString(), // 日
      'H+': date.getHours().toString(), // 时
      'm+': date.getMinutes().toString(), // 分
      's+': date.getSeconds().toString() // 秒
      // 有其他格式化字符需求可以继续添加，必须转化成字符串
    }
    for (let k in opt) {
      ret = new RegExp('(' + k + ')').exec(fmt)
      if (ret) {
        fmt = fmt.replace(ret[1], (ret[1].length === 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, '0')))
      }
    }
    return fmt
  }

  if (!fmt) {
    fmt = 'YYYY-MM-dd HH:mm:ss'
  }
  if (date instanceof Date) {
    return _fmt(date, fmt)
  }
  return _fmt(new Date(date), fmt)
}

export {formatDate}
