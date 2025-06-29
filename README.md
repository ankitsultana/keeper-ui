# Keeper UI - ZooKeeper UI and HTTP Proxy

**⚠️ Experimental Project**

<img src="https://github.com/ankitsultana/keeper-ui/blob/main/public/zk-browser-landing.png?raw=true" width=750>

Keeper UI is a complete UI and an HTTP proxy server for Apache ZooKeeper. You can check out a deployed version of the frontend at https://zk.ankitsultana.com.

## Features

- **Multi-Instance Support**: Connect to multiple ZooKeeper clusters simultaneously
- **RESTful HTTP Proxy**: Complete CRUD operations for ZooKeeper nodes
- **Node Statistics**: Retrieve detailed ZooKeeper node metadata
- **Ephemeral & Persistent Nodes**: Support for both node types
- **Configurable**: YAML-based configuration for easy deployment

## Architecture

<img src="https://github.com/ankitsultana/zookeeper-explorer-ui-plus/blob/main/public/lovable-uploads/e8d79631-97cb-4a44-a5ad-061f34ad3c05.png?raw=true" width=500>


## API Endpoints

All endpoints are prefixed with `/{instance}` where `instance` is the configured ZooKeeper instance name.

- `GET /{instance}/ls?path=/` - List children of a node
- `GET /{instance}/get?path=/` - Get node data, statistics, and children
- `POST /{instance}/create` - Create a new node
- `DELETE /{instance}/delete?path=/&version=-1` - Delete a node
- `POST /{instance}/set?path=/&version=-1` - Update node data

## Quick Start

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- Access to a ZooKeeper cluster

### Building

```bash
mvn clean package
```

### Running

Make sure you have a ZK cluster running and configured in the `application.yml` file.

#### With default configuration

```bash
java -jar target/keeper-ui-1.0-SNAPSHOT.jar
```

#### With custom configuration

```bash
java -jar target/keeper-ui-1.0-SNAPSHOT.jar /path/to/your/application.yml
```

The HTTP Proxy server will start on port 12345 by default. Next, you can spin up the Keeper UI or simply use the one hosted at https://zk.ankitsultana.com/

### Keeper UI Integration

#### Using zk.ankitsultana.com

Once your HTTP Proxy is up and able to connect to your Zookeeper instance (see Configuration section below), you can
go to zk.ankitsultana.com to browse your Zookeeper instance's contents.

#### Running UI Locally

You can also run the frontend locally by using the submodule under the `app` folder. Simply run `npm install` and `npm run dev`.

## Configuration

Create an `application.yml` file to configure the server and ZooKeeper instances:

```yaml
server:
  port: 12345

zookeeper:
  dev:
    host: localhost:9180
    session-timeout: 5000
  prod:
    host: prod-zk1:2181,prod-zk2:2181,prod-zk3:2181
    session-timeout: 10000
  local:
    host: localhost:2181
    session-timeout: 3000
```

### Configuration Options

#### Server Configuration
- `server.port`: HTTP server port (default: 12345)

#### ZooKeeper Instance Configuration
Each ZooKeeper instance supports:
- `host`: ZooKeeper connection string (single host or comma-separated cluster)
- `session-timeout`: Session timeout in milliseconds

### Example Usage

```bash
# List root children for 'dev' instance
curl "http://localhost:12345/dev/ls?path=/"

# Get node data for 'prod' instance
curl "http://localhost:12345/prod/get?path=/config"

# Create a new node
curl -X POST "http://localhost:12345/dev/create" \
  -H "Content-Type: application/json" \
  -d '{"path": "/test", "data": "hello world", "isEphemeral": false}'

# Delete a node
curl -X DELETE "http://localhost:12345/dev/delete?path=/test&version=-1"
```

## API Response Format

All API responses return JSON:

**Success responses:**
```json
{
  "children": ["node1", "node2"],
  "data": "node content",
  "stat": {
    "czxid": 123,
    "mzxid": 456,
    "version": 1,
    ...
  }
}
```

**Error responses:**
```json
{
  "message": "Error description"
}
```

## Development

### Project Structure

```
src/main/java/com/ankitsultana/keeper/ui/
├── KeeperUIApplication.java    # Main Spring Boot application
├── ZookeeperFacade.java       # ZooKeeper client wrapper
├── ZookeeperFactory.java      # Multi-instance connection factory
└── AppConfig.java             # Configuration binding
```

## Credits

- **UI**: Built with ❤️ by [lovable.dev](https://lovable.dev)
- **HTTP Proxy**: Built with [Claude](https://claude.ai) AI Assistant
- **Framework**: Spring Boot + Apache ZooKeeper Client

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*This is an experimental project created for educational and demonstration purposes.*
