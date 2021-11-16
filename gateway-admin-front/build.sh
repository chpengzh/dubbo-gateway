#!/bin/bash
if [[ -f ~/.nvm/nvm.sh ]]; then
    source ~/.nvm/nvm.sh
fi
if hash nvm 2>/dev/null; then
    nvm use v10.16.0
else
    echo 'Command nvm not found, use default node version !!'
fi
#可选版本如下：
#      v4.4.7
#      v5.12.0
#      v6.10.0
#      v7.8.0
#      v8.9.1
#      v9.11.2
#      v10.10.0
########################
# npm install and build
########################

echo "node version: "`node --version`
echo "npm version: "`npm -version`

#npm config set registry https://artifactory.intra.xiaojukeji.com/artifactory/api/npm/npm/

npm install --unsafe-perm=true --allow-root

npm run build
