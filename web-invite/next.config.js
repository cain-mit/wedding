/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
  // This is important when your app is deployed behind a proxy
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'https://wedding-api.srivatsa.dev/api/:path*'
      }
    ]
  }
}

module.exports = nextConfig
