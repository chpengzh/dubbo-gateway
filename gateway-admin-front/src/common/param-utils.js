export function toParamDescription(item) {
  // 将行信息转化为参数描述
  let result = ''

  // 1.必填/非必填描述
  switch (Number(item.required)) {
    case 0:
      result += '非必填'
      break
    case 1:
      result += '必填'
      break
    default:
      break
  }

  // 2.默认值描述
  if (item.paramType !== 60) {
    if (item.defaultValue) {
      result += ', 默认值:"' + item.defaultValue + '"'
    } else if (item.required === 0) {
      result += ', 默认空值'
    }
  }

  // 3.参数描述
  if (item.paramType === 60) {
    result += ', 对象类型' + item.className
  } else {
    result += ', ' + (item.description || '暂无描述')
  }
  return result
}

