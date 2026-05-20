
const http = require('http');

async function getToken() {
    const postData = JSON.stringify({
        userName: "ren.jiang",
        password: "Uds88888"
    });

    const options = {
        hostname: '139.159.221.11',
        port: 9002,
        path: '/qyplmapi/permission/user/login',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': Buffer.byteLength(postData)
        },
        timeout: 10000
    };

    return new Promise((resolve) => {
        const req = http.request(options, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => {
                try {
                    const json = JSON.parse(data);
                    if (json.code === '0' && json.data?.token) {
                        resolve({
                            success: true,
                            token: json.data.token,
                            message: '登录成功'
                        });
                    } else {
                        resolve({
                            success: false,
                            message: json.msg || '登录失败'
                        });
                    }
                } catch (err) {
                    resolve({
                        success: false,
                        message: '解析响应失败: ' + err.message
                    });
                }
            });
        });

        req.on('error', (err) => {
            resolve({
                success: false,
                message: '请求失败: ' + err.message
            });
        });

        req.on('timeout', () => {
            req.destroy();
            resolve({
                success: false,
                message: '请求超时'
            });
        });

        req.write(postData);
        req.end();
    });
}

if (require.main === module) {
    getToken().then(result => {
        console.log(JSON.stringify(result));
        process.exit(result.success ? 0 : 1);
    }).catch(err => {
        console.log(JSON.stringify({
            success: false,
            message: err.message
        }));
        process.exit(1);
    });
}

module.exports = { getToken };
EOF