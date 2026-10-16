config.resolve = {
    ...config.resolve,
    fallback: {
        ...config.resolve?.fallback,
        fs: false,
        path: false
    }
};


config.devServer = {
    ...config.devServer,
    allowedHosts: [
        "localhost",
        "shaneka-unfactual-shaneka.ngrok-free.dev",
        "coleman-nonethic-marinda.ngrok-free.dev"
    ]
};