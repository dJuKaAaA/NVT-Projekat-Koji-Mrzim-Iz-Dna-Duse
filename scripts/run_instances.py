import subprocess
import time

def run_instance(port):
    path = "./graalvm-demo.jar"
    command = f'java -jar {path} --server.port={port}'

    subprocess.Popen(['start', 'cmd', '/k', command], shell=True)

if __name__ == '__main__':
    run_instance(8081)
    time.sleep(1)

    run_instance(8082)
    time.sleep(1)

    run_instance(8083)