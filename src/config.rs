use std::env;
use std::sync::OnceLock;

#[derive(Debug)]
pub struct Config {
    pub datastore_root_dir: String,
    pub docker_host_name: String,
    pub commit_id: String,
    pub port: String,
    pub command: String,
}

static CONFIG: OnceLock<Config> = OnceLock::new();

pub fn init_config() {
    let config = Config {
        datastore_root_dir: env::var("DATASTORE_ROOT_DIR")
            .expect("Missing required environment variable: DATASTORE_ROOT_DIR"),
        docker_host_name: env::var("DOCKER_HOST_NAME")
            .expect("Missing required environment variable: DOCKER_HOST_NAME"),
        port: env::var("PORT").expect("Missing required environment variable: PORT"),

        commit_id: env::var("COMMIT_ID").expect("Missing required environment variable: COMMIT_ID"),
        command: env::args().collect::<Vec<String>>().join(" "),
    };
    let _ = CONFIG.set(config);
}

pub fn get_config() -> &'static Config {
    CONFIG
        .get()
        .expect("Config not initialized! Call init_config() first.")
}
