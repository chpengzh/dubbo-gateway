import GlobalConfig from "./global";

export function getEnv() {
  const currentHost = window.location.host
  const parallel = [
    {host: [GlobalConfig.env.prod], env: 'prod'},
    {host: [GlobalConfig.env.pre], env: 'pre'},
    {host: [GlobalConfig.env.stable], env: 'stable'},
    {host: [GlobalConfig.env.test], env: 'test'},
    {host: ['localhost', '127.0.0.1'], env: 'local'},
  ]
  for (let i = 0; i < parallel.length; ++i) {
    const next = parallel[i]
    for (let j = 0; j < next.host.length; ++j) {
      const match = next.host[j]
      if (currentHost.includes(match)) {
        return next.env
      }
    }
  }
  return 'local'
}