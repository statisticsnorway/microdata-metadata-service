use std::fmt::{self, Display};

use serde::{
    de::{self, Visitor},
    Deserialize, Serialize,
};

#[derive(Debug, Serialize)]
pub enum Version {
    DRAFT { timestamp: i32 },
    SEMANTIC { major: i16, minor: i16, patch: i16 },
}

impl Version {
    pub fn from_str(version_str: &str) -> Result<Version, serde_json::Error> {
        serde_json::from_str(&serde_json::to_string(version_str)?)
    }
    pub fn as_file_version(&self) -> String {
        match self {
            Version::DRAFT { .. } => "DRAFT".to_string(),
            Version::SEMANTIC {
                major,
                minor,
                patch,
            } => format!("{major}_{minor}_{patch}"),
        }
    }
}
impl Display for Version {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            Version::DRAFT { timestamp } => write!(f, "0.0.0.{timestamp}"),
            Version::SEMANTIC {
                major,
                minor,
                patch,
            } => write!(f, "{major}.{minor}.{patch}"),
        }
    }
}
impl PartialEq for Version {
    fn eq(&self, other: &Self) -> bool {
        match (self, other) {
            (Version::DRAFT { timestamp: a }, Version::DRAFT { timestamp: b }) => a == b,
            (
                Version::SEMANTIC {
                    major: a_major,
                    minor: a_minor,
                    patch: a_patch,
                },
                Version::SEMANTIC {
                    major: b_major,
                    minor: b_minor,
                    patch: b_patch,
                },
            ) => a_major == b_major && a_minor == b_minor && a_patch == b_patch,
            _ => false,
        }
    }
}

impl<'de> Deserialize<'de> for Version {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: de::Deserializer<'de>,
    {
        struct VersionVisitor;

        impl<'de> Visitor<'de> for VersionVisitor {
            type Value = Version;

            fn expecting(&self, formatter: &mut fmt::Formatter) -> fmt::Result {
                formatter.write_str(
                    "a version in the format '0.0.0.<timestamp>' or '<major>.<minor>.<patch>.0'",
                )
            }

            fn visit_str<E>(self, value: &str) -> Result<Self::Value, E>
            where
                E: de::Error,
            {
                if value.starts_with("0.0.0.") {
                    let timestamp: i32 = value[6..]
                        .parse()
                        .map_err(|_| E::custom("Invalid timestamp in DRAFT version"))?;
                    Ok(Version::DRAFT { timestamp })
                } else {
                    let parts: Vec<&str> = value.split('.').collect();
                    if parts.len() == 4 {
                        let major = parts[0]
                            .parse::<i16>()
                            .map_err(|_| E::custom("Invalid semantic version: major"))?;
                        let minor = parts[1]
                            .parse::<i16>()
                            .map_err(|_| E::custom("Invalid semantic version: minor"))?;
                        let patch = parts[2]
                            .parse::<i16>()
                            .map_err(|_| E::custom("Invalid semantic version: patch"))?;
                        if parts[3] != 0.to_string() {
                            return Err(E::custom("Invalid semantic version: draft"));
                        }
                        Ok(Version::SEMANTIC {
                            major,
                            minor,
                            patch,
                        })
                    } else {
                        Err(E::custom(format!(
                            "Invalid SEMANTIC version format: {value}"
                        )))
                    }
                }
            }
        }

        deserializer.deserialize_str(VersionVisitor)
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json;

    #[test]
    fn test_deserialization() {
        assert_eq!(
            serde_json::from_str::<Version>(r#""0.0.0.12345""#).unwrap(),
            Version::DRAFT { timestamp: 12345 }
        );
        assert_eq!(
            serde_json::from_str::<Version>(r#""1.2.3.0""#).unwrap(),
            Version::SEMANTIC {
                major: 1,
                minor: 2,
                patch: 3
            }
        );
    }

    #[test]
    fn test_invalid_deserialization() {
        let result: Result<Version, _> = serde_json::from_str(r#""0.0.0.invalid""#);
        assert!(result.is_err());
        let result: Result<Version, _> = serde_json::from_str(r#""1.2.3.1""#);
        assert!(result.is_err());
        let result: Result<Version, _> = serde_json::from_str(r#""1.2.3""#);
        assert!(result.is_err());
    }

    #[test]
    fn test_as_file_version() {
        assert_eq!(
            Version::DRAFT { timestamp: 12345 }.as_file_version(),
            "DRAFT"
        );
        assert_eq!(
            Version::SEMANTIC {
                major: 1,
                minor: 2,
                patch: 3,
            }
            .as_file_version(),
            "1_2_3"
        );
    }

    #[test]
    fn test_equality() {
        let draft = Version::DRAFT { timestamp: 12345 };
        let v1 = Version::SEMANTIC {
            major: 1,
            minor: 0,
            patch: 0,
        };
        let v2 = Version::SEMANTIC {
            major: 1,
            minor: 0,
            patch: 0,
        };
        let v3 = Version::SEMANTIC {
            major: 2,
            minor: 0,
            patch: 0,
        };

        assert_eq!(v1, v2);
        assert_ne!(v1, v3);
        assert_ne!(draft, v1);
    }
}
