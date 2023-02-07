import subprocess


def get_repository_directory():
    input_dir = input("Enter the path to your repository: ")
    return input_dir


def run_gradle_command(command):
    result = subprocess.run(["./gradlew run --args=", command], capture_output=True, text=True)
    if result.returncode == 0:
        print(result.stdout)
    else:
        print(f"Gradle command failed with exit code {result.returncode}: {result.stderr}")


def run_ast_parser():
    repo = get_repository_directory()
    run_gradle_command(f"ast --repository-path={repo}")


if __name__ == "__main__":
    run_ast_parser()
